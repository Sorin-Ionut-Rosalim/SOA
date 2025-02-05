'use client'
import PostForm from '@/feature/post/form/post-form'
import { useAuthStore } from '@/store/auth.store'
import React from 'react'

function CreatePostPage() {
    const user = useAuthStore((store) => store.user)
    if (!user) return <>not authenticated</>

    return (
        <div className="flex h-full w-full items-center justify-center">
            <PostForm user={user} />
        </div>
    )
}

export default CreatePostPage
