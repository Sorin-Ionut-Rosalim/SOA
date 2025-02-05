import { Post } from '@/types/post'
import React, { forwardRef } from 'react'
import Image from 'next/image'
import { Avatar, AvatarFallback, AvatarImage } from '../ui/avatar'
import { IoMdHeart, IoMdHeartEmpty } from 'react-icons/io'
import { Separator } from '../ui/separator'
import { cn } from '@/lib/utils'
import { formatDistanceToNow } from 'date-fns'
import { Button } from '../ui/button'

const getLikeOrDislikeIcon = (liked: boolean) => {
    if (!liked) return IoMdHeartEmpty

    return IoMdHeart
}

interface PostCardProps {
    post: Post
    canFollow: boolean
    first: boolean
    className?: string
    onLike: (postId: number) => void
    onDislike: (postId: number) => void
    onFollow: (username: string) => void
    onUnfollow: (username: string) => void
}
const PostCard = forwardRef<HTMLDivElement, PostCardProps>(
    (
        {
            post,
            onLike,
            onDislike,
            canFollow,
            onFollow,
            onUnfollow,
            first,
            className,
        },
        ref
    ) => {
        // const { mutate: like } = useLikePostMutation()
        // const { mutate: dislike } = useDislikePostMutation()
        const LikeIcon = getLikeOrDislikeIcon(post.liked)
        // Convert the string of Unix ms to a number, then to a Date
        const date = new Date(Number(post.createdAt))

        // Generate a relative time string like "2 days ago"
        const relativeDate = formatDistanceToNow(date, { addSuffix: true })

        return (
            <div
                className={cn('space-y-4 dark:bg-zinc-900', className)}
                ref={ref}
            >
                <Separator className="h-[1px] bg-fuchsia-600 dark:bg-zinc-600" />
                {/* Post header avatar + username */}
                <div className={cn('flex items-center justify-between')}>
                    <div className="flex items-center gap-4">
                        <Avatar>
                            <AvatarImage src={post.userAvatar} />
                            <AvatarFallback>{post.username}</AvatarFallback>
                        </Avatar>
                        <span className="text-lg font-bold dark:text-white">
                            {post.username}
                        </span>
                        &bull;
                        <span className="text-sm font-light dark:text-zinc-400">
                            {relativeDate}
                        </span>
                    </div>
                    {canFollow && (
                        <Button
                            type="button"
                            size={'sm'}
                            onClick={() =>
                                post.following
                                    ? onUnfollow(post.username)
                                    : onFollow(post.username)
                            }
                        >
                            {post.following ? 'Unfollow' : 'Follow'}
                        </Button>
                    )}
                </div>
                {/* Image */}
                <Image
                    alt={post.description}
                    width={480}
                    priority={first}
                    height={600}
                    className="fuchsia-600 rounded-sm border border-fuchsia-600 dark:border-0 dark:border-b-2 dark:border-zinc-600"
                    src={post.picUrl}
                />
                {/* Actions ( like basically ) */}
                <div>
                    <LikeIcon
                        onClick={() =>
                            post.liked ? onDislike(post.id) : onLike(post.id)
                        }
                        className={cn(
                            'transform select-none text-2xl transition-transform duration-300 ease-in-out hover:scale-110 hover:cursor-pointer',
                            post.liked
                                ? 'text-red-600'
                                : 'text-white hover:text-red-600'
                        )}
                    />
                </div>
                {/* Metadata ( likes) */}
                <div>
                    {post.likesCount} {post.likesCount == 1 ? 'like' : 'likes'}
                </div>
                {/* Description */}
                <div>{post.description}</div>
            </div>
        )
    }
)

PostCard.displayName = 'PostCard'
export default PostCard
