import { useAuthStore } from '@/store/auth.store'
import axios, { AxiosError } from 'axios'

class FollowService {
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

    public follow = async (followee: string) => {
        await this.axiosInstance.post(`/api/follow/${followee}`)
    }

    public unfollow = async (followee: string) => {
        await this.axiosInstance.delete(`/api/follow/${followee}`)
    }
}

export { FollowService }
