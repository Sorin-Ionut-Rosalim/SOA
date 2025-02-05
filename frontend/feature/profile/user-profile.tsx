import React, { memo } from 'react'
import UserProfileHeader from './user-profile-header'
import { User } from '@/types/identity'
import UserFeed from './user-feed'

const UserProfile: React.FC<{
    user: User
    sessionUser: User
    scrollToPost?: string | null
}> = memo(({ user, sessionUser, scrollToPost }) => {
    return (
        <div className="flex h-full w-[600px] flex-col items-center gap-5 bg-white dark:bg-zinc-900 dark:text-white">
            <UserProfileHeader user={user} />

            <UserFeed
                user={user}
                sessionUser={sessionUser}
                scrollToPost={scrollToPost}
            />
        </div>
    )
})
UserProfile.displayName = 'UserProfile'
export default UserProfile
