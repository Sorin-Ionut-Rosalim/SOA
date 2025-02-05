'use client'
// useSessionQuery.ts
import { useQuery } from '@tanstack/react-query'
import { useEffect } from 'react'
import { useAuthStore } from '@/store/auth.store'
import { User } from '@/types/identity'
import { getIdentityService } from '@/services/client-services'

const useSessionQuery = () => {
    const setUser = useAuthStore((state) => state.setUser)

    // Perform the query as usual
    const queryResult = useQuery<User>({
        queryKey: ['session'],
        queryFn: async () => {
            // Ensure that getSession returns the user data
            const user = await getIdentityService().getSession()
            return user
        },
    })

    // Use an effect to update the Zustand store when data is fetched
    useEffect(() => {
        if (queryResult.data) {
            setUser(queryResult.data)
        }
        // Optionally, if you want to clear the store on error:
        if (queryResult.error) {
            setUser(undefined)
        }
    }, [queryResult.data, queryResult.error, setUser])

    return queryResult
}

const useGetUserQuery = (username: string) => {
    return useQuery({
        queryKey: ['user', username],
        queryFn: async () => await getIdentityService().getUser(username),
    })
}

export { useSessionQuery, useGetUserQuery }
