import { Post } from '@/types/post'
import React, { memo, useEffect, useRef, useState } from 'react'
import PostCard from './post-card'
import { User } from '@/types/identity'
import {
    useDislikePostMutation,
    useLikePostMutation,
} from '@/hooks/mutations/post'
import {
    useFollowMutation,
    useUnfollowMutation,
} from '@/hooks/mutations/follow'
import { cn } from '@/lib/utils'
const HIGHLIGHT_DURATION = 2800

const PostList: React.FC<{
    posts: Post[]
    user: User
    scrollToPost?: string | null
}> = memo(({ posts, user, scrollToPost }) => {
    const { mutate: likePost } = useLikePostMutation()
    const { mutate: dislikePost } = useDislikePostMutation()
    const { mutate: followUser } = useFollowMutation()
    const { mutate: unfollowUser } = useUnfollowMutation()
    const postRefs = useRef<Record<string, HTMLDivElement | null>>({})

    const [highlightedPostId, setHighlightedPostId] = useState<string | null>(
        null
    )
    useEffect(() => {
        if (!scrollToPost) {
            setHighlightedPostId(null)
            return
        }
        const targetRef = postRefs.current[scrollToPost]
        if (targetRef) {
            // Scroll into view smoothly, centered
            targetRef.scrollIntoView({ behavior: 'smooth', block: 'center' })

            // Highlight
            setHighlightedPostId(scrollToPost)

            // Remove highlight after a short delay
            const timer = setTimeout(() => {
                setHighlightedPostId(null)
            }, HIGHLIGHT_DURATION)

            return () => clearTimeout(timer)
        }
    }, [scrollToPost])

    return (
        <div className="h-full min-w-[480px] space-y-4 bg-white px-28 dark:bg-zinc-900">
            {posts.map((post, idx) => {
                const isHighlighted = highlightedPostId === String(post.id)

                return (
                    <PostCard
                        first={idx === 0}
                        key={post.id}
                        post={post}
                        onLike={likePost}
                        onDislike={dislikePost}
                        onFollow={followUser}
                        onUnfollow={unfollowUser}
                        className={cn(
                            'transition-colors duration-700 ease-in-out',
                            isHighlighted && // Animate a pulsing teal background + ring
                                'animate-pulse'
                        )}
                        canFollow={post.username != user.username}
                        ref={(el) => {
                            postRefs.current[post.id] = el
                            return
                        }}
                    />
                )
            })}
        </div>
    )
})

PostList.displayName = 'PostList'
export default PostList
