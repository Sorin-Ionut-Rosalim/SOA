'use client'
import NotificationList from '@/feature/notification/notification-list'
import { useNotifications } from '@/providers/notifications-provider'
import React, { useEffect } from 'react'

function NotificationsPage() {
    const { markAllAsRead, unread, notifications } = useNotifications()

    useEffect(() => {
        if (unread) markAllAsRead()
    }, [markAllAsRead, unread])

    return (
        <div className="flex h-full w-full justify-center">
            <NotificationList notifications={notifications} />
        </div>
    )
}

export default NotificationsPage
