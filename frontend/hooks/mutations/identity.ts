'use client'
import { getIdentityService } from '@/services/client-services'
import { LoginUser, RegisterUser } from '@/types/identity'
import { useMutation } from '@tanstack/react-query'

import { toast } from 'react-toastify'
import { useRouter } from 'next/navigation'
import { z } from 'zod'
import { useAuthStore } from '@/store/auth.store'

const useRegisterMutation = () => {
    const router = useRouter()
    return useMutation({
        mutationFn: async (registerUser: RegisterUser) => {
            const identityService = getIdentityService()
            await identityService.register(registerUser)
        },
        onSuccess: () => {
            toast.success('Successfully registered')
            router.push('/login')
        },
        onError: (error: unknown) => {
            const res = z
                .object({
                    message: z.string(),
                })
                .safeParse(error)
            if (res.error) return

            toast.error(res.data.message)
        },
    })
}

const useLoginMutation = () => {
    const router = useRouter()
    return useMutation({
        mutationFn: async (loginUser: LoginUser) => {
            const identityService = getIdentityService()
            await identityService.login(loginUser)
        },
        onSuccess: () => {
            toast.success('Successfully logged in')
            router.push('/home')
        },
        onError: (error: unknown) => {
            const res = z
                .object({
                    message: z.string(),
                })
                .safeParse(error)
            if (res.error) return

            toast.error(res.data.message)
        },
    })
}

const useLogoutMutation = () => {
    const router = useRouter()
    const setUser = useAuthStore((store) => store.setUser)

    return useMutation({
        mutationFn: async () => {
            await getIdentityService().logout()
        },
        onSuccess: async () => {
            await router.push('/login')
            setUser(undefined)
        },
        onError: (err) => toast.error(`failed to logout ${err.message}`),
    })
}
export { useRegisterMutation, useLoginMutation, useLogoutMutation }
