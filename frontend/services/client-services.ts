'use client'

import { FollowService } from './follow'
import { IdentityService } from './identity'
import { PostService } from './post'

export function getIdentityService() {
    return new IdentityService(
        process.env.NEXT_PUBLIC_IDENTITY_SERVICE_URL as string
    )
}

export function getPostService() {
    return new PostService(process.env.NEXT_PUBLIC_POST_SERVICE_URL as string)
}

export function getFollowService() {
    return new FollowService(
        process.env.NEXT_PUBLIC_FOLLOW_SERVICE_URL as string
    )
}
