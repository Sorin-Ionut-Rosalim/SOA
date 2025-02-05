import React, { memo } from 'react'
import { Avatar, AvatarFallback, AvatarImage } from '@/components/ui/avatar'
import { User } from '@/types/identity'
const UserProfileHeader: React.FC<{ user: User }> = memo(({ user }) => {
    return (
        <div className="flex w-full flex-col items-center justify-center overflow-visible">
            <Avatar className="size-40 border object-cover dark:border-4 dark:border-zinc-900">
                <AvatarImage src={user.profilePic} />
                <AvatarFallback>{user.username}</AvatarFallback>
            </Avatar>

            <div className="px-6 text-3xl font-bold">
                <p className="">{user.username}</p>
            </div>
        </div>
    )
})
UserProfileHeader.displayName = 'UserProfileHeader'

export default UserProfileHeader
