import React, { memo } from 'react'
import { Notification } from '@/providers/notifications-provider'
import NotificationItem from './notification-item'

const NotificationList: React.FC<{
    notifications: Notification[]
}> = memo(({ notifications }) => {
    return (
        <div className="scrollbar-thin scrollbar-track-black scrollbar-thumb-neutral-800 flex w-full max-w-2xl flex-col gap-5 overflow-y-auto bg-white p-6 px-10 pt-10 dark:bg-zinc-800">
            {notifications.map((notification) => (
                <NotificationItem
                    key={notification.id}
                    notification={notification}
                />
            ))}
        </div>
    )
})

NotificationList.displayName = 'NotificationList'
export default NotificationList
