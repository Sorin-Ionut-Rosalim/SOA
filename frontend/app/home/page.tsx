'use client'

import PostList from '@/components/post/post-list'
import { useGetAllPosts } from '@/hooks/queries/post'
import { cn } from '@/lib/utils'
import { useAuthStore } from '@/store/auth.store'
import { FeedType, feedTypeSchema } from '@/types/post'
import { useState } from 'react'

export default function Home() {
    const user = useAuthStore((store) => store.user)
    const [feedType, setFeedType] = useState<FeedType>(
        feedTypeSchema.Values.ALL
    )
    const postsQuery = useGetAllPosts(feedType)

    if (!user) return <>unauthenticated</>
    // if (postsQuery.isLoading || postsQuery.isFetching || postsQuery.isPending) {
    //     return (
    //         <div className="h-full w-full flex-col items-center justify-center text-center font-[family-name:var(--font-geist-sans)]">
    //             ...loading
    //         </div>
    //     )
    // }
    if (postsQuery.isError) {
        return <>{postsQuery.error.message}</>
    }

    const posts = postsQuery.data ?? []

    return (
        <div className="overflow-x-clipfont-[family-name:var(--font-geist-sans)] flex min-h-screen flex-col items-center gap-10">
            <div className="flex h-full min-h-screen flex-col bg-white pb-10 dark:bg-zinc-900">
                <div className="self-start px-28 pt-9">
                    <div className="flex gap-5 pb-2 text-xl dark:text-zinc-600">
                        <button
                            type="button"
                            className={cn(
                                feedType === 'ALL'
                                    ? 'font-bold dark:text-zinc-200'
                                    : ''
                            )}
                            onClick={() => setFeedType('ALL')}
                        >
                            All
                        </button>
                        <button
                            type="button"
                            className={cn(
                                feedType === 'FOLLOWING'
                                    ? 'font-bold dark:text-zinc-200'
                                    : ''
                            )}
                            onClick={() => setFeedType('FOLLOWING')}
                        >
                            Following
                        </button>
                    </div>
                </div>
                <PostList posts={posts} user={user} />
            </div>
        </div>
    )
}
