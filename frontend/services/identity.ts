import { useAuthStore } from '@/store/auth.store'
import { userSchema, type RegisterUser, type LoginUser } from '@/types/identity'
import axios, { AxiosError } from 'axios'

class IdentityService {
    private axiosInstance
    constructor(baseURL: string) {
        this.axiosInstance = axios.create({
            baseURL: baseURL,
            withCredentials: true,
        })
        this.axiosInstance.interceptors.response.use(
            (res) => res,
            (error: AxiosError) => {
                if (error?.status === 401) {
                    useAuthStore.setState({ user: undefined })
                    window.history.pushState(null, '', '/login')
                }

                return Promise.reject(error)
            }
        )
    }

    public getUser = async (username: string) => {
        const res = await this.axiosInstance.get(`/api/user/${username}`)
        return userSchema.parse(res.data)
    }

    public getSession = async () => {
        const res = await this.axiosInstance.get('/api/session')
        return userSchema.parse(res.data)
    }

    public register = async (registerUser: RegisterUser) => {
        const res = await this.axiosInstance.post(
            '/api/auth/register',
            registerUser
        )
        return userSchema.parse(res.data)
    }

    public login = async (loginUser: LoginUser) => {
        const res = await this.axiosInstance.post('/api/auth/login', loginUser)
        return userSchema.parse(res.data)
    }

    public logout = async () => {
        await this.axiosInstance.post('/api/auth/logout')
    }
}

export { IdentityService }
