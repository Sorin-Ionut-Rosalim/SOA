'use client'

import UserProfile from '@/feature/profile/user-profile'
import { useGetUserQuery, useSessionQuery } from '@/hooks/queries/identity'

import { useParams, useSearchParams } from 'next/navigation'

export default function UserProfilePage() {
    const { username } = useParams<{ username: string }>()
    const scrollToPost = useSearchParams().get('post')

    const userQuery = useGetUserQuery(username)
    const sessionQuery = useSessionQuery()

    if (
        sessionQuery.isLoading ||
        sessionQuery.isPending ||
        sessionQuery.isFetching
    ) {
        return <div>loading...</div>
    }
    if (sessionQuery.isError) {
        return <div>{sessionQuery.error.message}</div>
    }

    if (userQuery.isLoading || userQuery.isPending || userQuery.isFetching) {
        return <div>loading...</div>
    }
    if (userQuery.isError) {
        return <div>{userQuery.error.message}</div>
    }

    const sessionUser = sessionQuery.data
    const user = userQuery.data

    return (
        <div className="flex h-full w-full justify-center">
            <UserProfile
                user={user}
                sessionUser={sessionUser}
                scrollToPost={scrollToPost}
            />
        </div>
    )
}
