import { z } from 'zod'

const postSchema = z.object({
    id: z.number(),
    description: z.string(),
    picUrl: z.string().url(),
    createdAt: z.string(),
    likesCount: z.number(),
    userAvatar: z.string(),
    username: z.string(),
    following: z.boolean(),
    liked: z.boolean(),
})

type Post = z.infer<typeof postSchema>

const createPostSchema = z.object({
    description: z.string().min(1),
    picUrl: z.string().url(),
})

type CreatePost = z.infer<typeof createPostSchema>

const feedTypeSchema = z.enum(['ALL', 'FOLLOWING'])
type FeedType = z.infer<typeof feedTypeSchema>

export type { Post, CreatePost, FeedType }
export { postSchema, createPostSchema, feedTypeSchema }
