'use client'
import { Form, FormField } from '@/components/ui/form'
import { User } from '@/types/identity'

import { zodResolver } from '@hookform/resolvers/zod'
import { memo, useRef } from 'react'
import { useForm } from 'react-hook-form'
import DescriptionField from './description-field'
import { Button } from '@/components/ui/button'
import { useCreatePostMutation } from '@/hooks/mutations/post'
import { useUploadThing } from '@/hooks/upload-thing/helpers'
import PictureField from './picture-field'
import { z } from 'zod'
import { Id, toast } from 'react-toastify'

const postFormSchema = z.object({
    description: z.string().min(1).max(256),
    picture: z.instanceof(File),
})

type PostForm = z.infer<typeof postFormSchema>

const PostForm: React.FC<{ user: User }> = memo(() => {
    const toastId = useRef<Id>(null)
    const form = useForm<PostForm>({
        resolver: zodResolver(postFormSchema),
        defaultValues: {
            description: '',
        },
    })
    const { startUpload } = useUploadThing('imageUploader', {
        onUploadError: (e) => {
            toast.error(`failed to upload image ${e.message}`)
        },
        onClientUploadComplete: () => {
            if (toastId.current) toast.done(toastId.current)
        },
        onUploadProgress: (p) => {
            const progress = p / 100

            if (toastId.current === null) {
                toastId.current = toast('Upload in progress', { progress })
            } else {
                toast.update(toastId.current, { progress })
            }
        },
    })
    const { mutate: createPost } = useCreatePostMutation()

    async function onSubmit(values: PostForm) {
        try {
            const resArray = await startUpload([values.picture])

            if (resArray && (resArray?.length ?? 0) > 0) {
                const res = resArray[0]
                createPost({
                    description: values.description,
                    picUrl: res.url,
                })
            }
        } catch (error: unknown) {
            toast.error(`failed to create post: ${error}`)
        }
    }
    const pictureValue = form.watch('picture')
    return (
        <Form {...form}>
            <form
                onSubmit={form.handleSubmit(onSubmit)}
                className="flex w-[700px] flex-col items-center justify-center gap-5 rounded-xl bg-white p-10 dark:bg-zinc-900"
            >
                <FormField
                    control={form.control}
                    name="picture"
                    render={({ field }) => <PictureField {...field} />}
                />
                {pictureValue && (
                    <FormField
                        control={form.control}
                        name="description"
                        rules={{
                            maxLength: 256,
                            max: 256,
                        }}
                        render={({ field }) => <DescriptionField {...field} />}
                    />
                )}
                {pictureValue && (
                    <Button
                        type="submit"
                        className="bg-fuchsia-600 px-10 hover:bg-fuchsia-800 dark:bg-blue-600 dark:text-white dark:hover:bg-blue-800"
                    >
                        Submit
                    </Button>
                )}
            </form>
        </Form>
    )
})

PostForm.displayName = 'PostForm'

export default PostForm
