import { NextResponse } from 'next/server'
import type { NextRequest } from 'next/server'

import axios from 'axios'
import { userSchema } from './types/identity'

export async function middleware(request: NextRequest) {
    const authCookie = request.cookies.get('notagram-auth-token')
    if (!authCookie) {
        return NextResponse.redirect(new URL('/login', request.url))
    }

    try {
        // This call will include the cookie in the request to the identity service
        console.log('Trying to fetch session')

        const res = await axios.get(
            `${process.env.IDENTITY_SERVICE_URL as string}/api/session`,
            {
                withCredentials: true,
                headers: {
                    Cookie: `notagram-auth-token=${authCookie.value}`,
                },
            }
        )

        const user = userSchema.parse(res.data)

        console.log(
            '🚀 ~ middleware ~ request.pathname:',
            request.nextUrl.pathname
        )

        const nextUrl = request.nextUrl
        if (request.nextUrl.pathname === '/home/profile') {
            nextUrl.pathname = `/home/profile/${user.username}`

            return NextResponse.redirect(nextUrl)
        }

        // Optionally, attach user info to the request if needed
        return NextResponse.next()
    } catch (error) {
        console.error(error)
        return NextResponse.redirect(new URL('/login', request.url))
    }
}

export const config = {
    matcher: [
        '/((?!api|_next/static|_next/image|favicon.ico|sitemap.xml|robots.txt|login|register).*)',
    ],
}
