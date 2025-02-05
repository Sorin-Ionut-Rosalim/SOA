import React, { forwardRef } from 'react'
import { ControllerRenderProps } from 'react-hook-form'
import {
    FormControl,
    FormItem,
    FormLabel,
    FormMessage,
} from '@/components/ui/form'
import { CreatePost } from '@/types/post'
import { Textarea } from '@/components/ui/textarea'

const DescriptionField = forwardRef<
    HTMLTextAreaElement,
    ControllerRenderProps<CreatePost, 'description'>
>((field, ref) => {
    return (
        <FormItem className="w-full">
            <FormLabel>Description</FormLabel>
            <FormControl>
                <Textarea placeholder="" {...field} ref={ref} />
            </FormControl>
            <FormMessage />
        </FormItem>
    )
})
DescriptionField.displayName = 'DescriptionField'

export default React.memo(DescriptionField)
