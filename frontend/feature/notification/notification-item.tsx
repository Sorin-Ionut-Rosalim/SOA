import React, { memo, useMemo } from 'react'
import type { Notification } from '@/providers/notifications-provider'
import Link from 'next/link'

const NotificationItem: React.FC<{ notification: Notification }> = memo(
    ({ notification }) => {
        const content = useMemo(() => {
            const { content } = notification
            switch (content.type) {
                case 'NEW_POST':
                    return (
                        <div className="text-center">
                            <Link href={`/home/profile/${content.username}`}>
                                {content.username}{' '}
                            </Link>
                            posted something new! Check his profile
                        </div>
                    )
                case 'FOLLOW':
                    return (
                        <div className="text-center">
                            <Link href={`/home/profile/${content.follower}`}>
                                {content.follower}{' '}
                            </Link>{' '}
                            <span>
                                has {content.action.toLowerCase()}ed you
                            </span>
                        </div>
                    )
                case 'LIKE':
                    return (
                        <div className="text-center">
                            <Link href={`/home/profile/${content.username}`}>
                                {content.username}{' '}
                            </Link>{' '}
                            <span>
                                has {content.action.toLowerCase()}d your{' '}
                                <Link
                                    href={`/home/profile?post=${content.postId}`}
                                >
                                    post
                                </Link>
                            </span>
                        </div>
                    )
            }
        }, [notification])
        return (
            <div className="group w-full rounded-md bg-gradient-to-l from-[#fb7185] via-[#a21caf] to-[#6366f1] p-4 text-xl font-semibold transition-colors dark:from-[#0f172a] dark:via-[#1e1a78] dark:to-[#0f172a] dark:text-white">
                {content}
            </div>
        )
    }
)

NotificationItem.displayName = 'NotificationItem'
export default NotificationItem
