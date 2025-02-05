import PostList from '@/components/post/post-list'
import { useGetAllPosts } from '@/hooks/queries/post'
import { User } from '@/types/identity'
import React, { memo } from 'react'

const UserFeed: React.FC<{
    user: User
    sessionUser: User
    scrollToPost?: string | null
}> = memo(({ user, sessionUser, scrollToPost }) => {
    const postsQuery = useGetAllPosts('ALL', user.username)

    if (postsQuery.isLoading || postsQuery.isPending || postsQuery.isFetching) {
        return <div>loading...</div>
    }
    if (postsQuery.isError) {
        return <div>{postsQuery.error.message}</div>
    }
    const posts = postsQuery.data
    return (
        <div className="w-full py-5">
            <PostList
                posts={posts}
                user={sessionUser}
                scrollToPost={scrollToPost}
            />
        </div>
    )
})

UserFeed.displayName = 'UserFeed'

export default UserFeed
