import { getPostService } from '@/services/client-services'
import { FeedType } from '@/types/post'
import { useQuery } from '@tanstack/react-query'

const useGetAllPosts = (feedType: FeedType, username?: string) => {
    return useQuery({
        queryKey: ['posts', feedType, username],
        queryFn: async () =>
            await getPostService().getAllPosts(feedType, username),
    })
}

export { useGetAllPosts }
