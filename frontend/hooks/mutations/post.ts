'use client'

import { getPostService } from '@/services/client-services'
import { CreatePost } from '@/types/post'
import { useMutation, useQueryClient } from '@tanstack/react-query'
import { useRouter } from 'next/navigation'
import { toast } from 'react-toastify'

const useCreatePostMutation = () => {
    const qc = useQueryClient()
    const { push } = useRouter()
    return useMutation({
        mutationFn: async (createPost: CreatePost) => {
            const postService = getPostService()
            return await postService.createPost(createPost)
        },
        onSettled: () => {
            qc.invalidateQueries({
                queryKey: ['posts'],
            })
        },
        onSuccess: (data) => {
            toast.success('Posted successfully')
            push(`/home/profile?post=${data.id}`)
        },
        onError: (e) => {
            toast.error(`Failed to post: ${e.message}`)
        },
    })
}

const useLikePostMutation = () => {
    const qc = useQueryClient()
    return useMutation({
        mutationFn: async (postId: number) => {
            await getPostService().likePost(postId)
        },
        onSettled: () => {
            qc.invalidateQueries({
                queryKey: ['posts'],
            })
        },
        onError: (err) => {
            toast.error(`failed to like post: ${err.message}`)
        },
    })
}
const useDislikePostMutation = () => {
    const qc = useQueryClient()
    return useMutation({
        mutationFn: async (postId: number) => {
            await getPostService().dislikePost(postId)
        },
        onSettled: () => {
            qc.invalidateQueries({
                queryKey: ['posts'],
            })
        },
        onError: (err) => {
            toast.error(`failed to like post: ${err.message}`)
        },
    })
}

export { useCreatePostMutation, useLikePostMutation, useDislikePostMutation }
