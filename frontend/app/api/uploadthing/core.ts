import { User, userSchema } from '@/types/identity'
import axios from 'axios'
import { cookies } from 'next/headers'
import { createUploadthing, type FileRouter } from 'uploadthing/next'
import { UploadThingError } from 'uploadthing/server'

const f = createUploadthing()

// FileRouter for your app, can contain multiple FileRoutes
export const ourFileRouter = {
    // Define as many FileRoutes as you like, each with a unique routeSlug
    imageUploader: f({
        image: {
            /**
             * For full list of options and defaults, see the File Route API reference
             * @see https://docs.uploadthing.com/file-routes#route-config
             */
            maxFileSize: '8MB',
            maxFileCount: 1,
        },
    })
        // Set permissions and file types for this FileRoute
        .middleware(async () => {
            // This code runs on your server before upload

            // Get the cookie store using Next.js's built-in helper
            const cookieStore = await cookies()
            const authCookie = cookieStore.get('notagram-auth-token')

            if (!authCookie) {
                throw new UploadThingError('Unauthorized')
            }

            let user: User | undefined
            try {
                // Manually forward the cookie value by setting the Cookie header.
                const res = await axios.get(
                    `${process.env.IDENTITY_SERVICE_URL as string}/api/session`,
                    {
                        headers: {
                            // Send the cookie header explicitly so that the identity service sees it.
                            Cookie: `notagram-auth-token=${authCookie.value}`,
                        },
                        // withCredentials: true is not enough on the server side.
                    }
                )
                user = userSchema.parse(res.data)
            } catch (error: unknown) {
                console.log(error)
            }
            if (!user) throw new UploadThingError('Unauthorized')
            return { username: user.username }
        })
        .onUploadError(({ error }) => {
            console.error(error)
        })
        .onUploadComplete(async ({ metadata, file }) => {
            // This code RUNS ON YOUR SERVER after upload

            // Whatever is returned here is sent to the clientside onClientUploadComplete callback
            return { uploadedBy: metadata.username, fileUrl: file.url }
        }),
} satisfies FileRouter

export type OurFileRouter = typeof ourFileRouter
