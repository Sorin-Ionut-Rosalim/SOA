import React, {
    createContext,
    useState,
    useEffect,
    useCallback,
    useRef,
    useMemo,
    useContext,
} from 'react'
import { Stomp, StompSubscription } from '@stomp/stompjs'
import SockJS from 'sockjs-client'
import { notificationContent, NotificationContent } from '@/types/notification'

// We'll store an array of notifications or at least an unread count
export interface Notification {
    content: NotificationContent
    read: boolean
    id: string
    // ... other fields if needed
}

interface NotificationsContextValue {
    notifications: Notification[]
    unread: boolean
    markAllAsRead: () => void
}

export const NotificationsContext = createContext<NotificationsContextValue>({
    notifications: [],
    unread: false,
    markAllAsRead: () => {},
})

export const useNotifications = () => useContext(NotificationsContext)

const notificationServiceUrl = process.env
    .NEXT_PUBLIC_NOTIFICATION_SERVICE_URL as string

export const NotificationsProvider: React.FC<{ children: React.ReactNode }> = ({
    children,
}) => {
    const [notifications, setNotifications] = useState<Notification[]>([])
    const subscriptionRef = useRef<StompSubscription>(null)
    const unread = useMemo(
        () => notifications.some((n) => !n.read),
        [notifications]
    )

    // WebSocket / STOMP connection logic
    useEffect(() => {
        const stompClient = Stomp.over(() => {
            return new SockJS(`${notificationServiceUrl}/ws`)
        })

        stompClient.connect(
            {},
            () => {
                subscriptionRef.current = stompClient.subscribe(
                    '/user/queue/notifications',
                    (message) => {
                        console.log(message.body)
                        const newNotification: Notification = {
                            read: false,
                            content: notificationContent.parse(
                                JSON.parse(message.body)
                            ),
                            id: message.headers['message-id'],
                        }
                        // Assume server sets read=false by default
                        setNotifications((prev) => [newNotification, ...prev])
                    }
                )
            },
            (error: unknown) => {
                console.error('STOMP connection error:', error)
            }
        )

        return () => {
            if (subscriptionRef.current) subscriptionRef.current.unsubscribe()
            stompClient.disconnect()
        }
    }, [])

    const markAllAsRead = useCallback(() => {
        // 1) Send an update to the server if needed or just mark locally
        setNotifications((prev) => prev.map((n) => ({ ...n, read: true })))

        // Optionally call an API to persist that "read" state, or send a STOMP message
    }, [])

    return (
        <NotificationsContext.Provider
            value={{ notifications, markAllAsRead, unread }}
        >
            {children}
        </NotificationsContext.Provider>
    )
}
