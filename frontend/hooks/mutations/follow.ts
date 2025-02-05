import { getFollowService } from '@/services/client-services'
import { useMutation, useQueryClient } from '@tanstack/react-query'
import { toast } from 'react-toastify'

const useFollowMutation = () => {
    const qc = useQueryClient()
    return useMutation({
        mutationFn: getFollowService().follow,
        onError: (error) => toast.error(`failed to follow: ${error.message}`),
        onSuccess: () => {
            qc.invalidateQueries({
                queryKey: ['posts'],
            })
        },
    })
}
const useUnfollowMutation = () => {
    const qc = useQueryClient()

    return useMutation({
        mutationFn: getFollowService().unfollow,
        onError: (error) => toast.error(`failed to unfollow: ${error.message}`),
        onSuccess: () => {
            qc.invalidateQueries({
                queryKey: ['posts'],
            })
        },
    })
}

export { useFollowMutation, useUnfollowMutation }
