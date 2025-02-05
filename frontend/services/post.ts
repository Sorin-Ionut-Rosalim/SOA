import { useAuthStore } from '@/store/auth.store'
import { FeedType, postSchema, type CreatePost } from '@/types/post'
import axios, { AxiosError } from 'axios'

class PostService {
    private axiosInstance
    constructor(baseURL: string) {
        this.axiosInstance = axios.create({
            baseURL: baseURL,
            withCredentials: true,
        })
        this.axiosInstance.interceptors.response.use(
            (res) => res,
            (error: AxiosError) => {
                if (error.status === 401) {
                    useAuthStore.setState({ user: undefined })
                    window.history.pushState(null, '', '/login')
                }

                return Promise.reject(error)
            }
        )
    }

    public createPost = async (createPost: CreatePost) => {
        const res = await this.axiosInstance.post('/api/post', createPost)
        return postSchema.parse(res.data)
    }

    public getAllPosts = async (feedType: FeedType, username?: string) => {
        const url = username
            ? `/api/post?username=${username}`
            : `/api/post?feedType=${feedType}`
        const res = await this.axiosInstance.get(url)
        return postSchema.array().parse(res.data)
    }

    public likePost = async (postId: number) => {
        const res = await this.axiosInstance.post(`/api/post/like/${postId}`)
        return postSchema.parse(res.data)
    }

    public dislikePost = async (postId: number) => {
        const res = await this.axiosInstance.delete(`/api/post/like/${postId}`)
        return postSchema.parse(res.data)
    }
}

export { PostService }
