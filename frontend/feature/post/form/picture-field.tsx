import React, { forwardRef, useState, useCallback, useRef } from 'react'

import { ControllerRenderProps } from 'react-hook-form'
import { FormControl, FormItem, FormMessage } from '@/components/ui/form'
import { useDropzone } from '@uploadthing/react'
import Cropper, { ReactCropperElement } from 'react-cropper'
import 'cropperjs/dist/cropper.css'
import { Button } from '@/components/ui/button'
import { MdArrowBackIos } from 'react-icons/md'
import Image from 'next/image'
function dataURLtoFile(dataURL: string, filename: string): File {
    const arr = dataURL.split(',')
    const mimeMatch = arr[0].match(/:(.*?);/)
    if (!mimeMatch) throw new Error('Invalid data URL')
    const mime = mimeMatch[1]
    const bstr = atob(arr[1])
    let n = bstr.length
    const u8arr = new Uint8Array(n)
    while (n--) {
        u8arr[n] = bstr.charCodeAt(n)
    }
    return new File([u8arr], filename, { type: mime })
}

function readFile(file: File): Promise<string> {
    return new Promise((resolve, reject) => {
        const reader = new FileReader()
        reader.addEventListener('load', () => resolve(reader.result as string))
        reader.addEventListener('error', reject)
        reader.readAsDataURL(file)
    })
}

const PictureField = forwardRef<
    HTMLInputElement,
    ControllerRenderProps<{ picture: File }, 'picture'>
    // eslint-disable-next-line @typescript-eslint/no-unused-vars
>(({ onChange, value }, _ref) => {
    const [imageSrc, setImageSrc] = useState<string | null>(null)
    const onDrop = useCallback(async (acceptedFiles: File[]) => {
        const file = acceptedFiles.at(0)
        if (file) {
            const imageDataUrl = await readFile(file)
            setImageSrc(imageDataUrl)
        }
    }, [])
    const { getRootProps, getInputProps, isDragActive } = useDropzone({
        onDrop,
        multiple: false,
    })
    const cropperRef = useRef<ReactCropperElement>(null)

    const setFieldValue = useCallback(() => {
        const cropper = cropperRef.current?.cropper

        if (cropper) {
            const croppedImageData = cropper.getCroppedCanvas().toDataURL()
            const croppedImageFile = dataURLtoFile(
                croppedImageData,
                'cropped-image.jpg'
            )
            onChange(croppedImageFile)
            setImageSrc(croppedImageData)
        }
    }, [onChange])

    return (
        <FormItem className="flex w-full flex-col items-center justify-center">
            <FormControl>
                {imageSrc === null ? (
                    <div
                        className="flex h-[600px] w-[480px] items-center justify-center border border-dotted border-black dark:border-white"
                        {...getRootProps()}
                    >
                        <input {...getInputProps()} />
                        {isDragActive ? (
                            <p>Drop the files here ...</p>
                        ) : (
                            <p>
                                Drag &apos;n&apos; drop some files here, or
                                click to select files
                            </p>
                        )}
                    </div>
                ) : (
                    <div className="flex flex-col">
                        <div className="flex w-full items-center justify-between rounded-t-xl border-b border-black bg-fuchsia-600 px-2 py-1 dark:bg-blue-700">
                            <Button
                                type="button"
                                variant={'ghost'}
                                size={'icon'}
                                className="z-50"
                                onClick={() => {
                                    setImageSrc(null)
                                    onChange(undefined)
                                }}
                            >
                                <MdArrowBackIos />
                            </Button>
                            {!Boolean(value) && (
                                <div className="text-center">Cropping</div>
                            )}
                            {!Boolean(value) && (
                                <Button
                                    type="button"
                                    variant={'ghost'}
                                    size={'icon'}
                                    className="z-50 font-semibold"
                                    onClick={setFieldValue}
                                >
                                    Next
                                </Button>
                            )}
                        </div>
                        {!Boolean(value) ? (
                            <div className="h-[600px] w-[480px]">
                                <Cropper
                                    src={imageSrc}
                                    className="min-h-full max-w-full"
                                    autoCropArea={1}
                                    cropBoxResizable={false}
                                    autoCrop
                                    dragMode="move"
                                    viewMode={3}
                                    ref={cropperRef}
                                />
                            </div>
                        ) : (
                            <Image
                                alt="image to post"
                                width={480}
                                height={600}
                                src={imageSrc}
                            />
                        )}
                    </div>
                )}
            </FormControl>
            <FormMessage />
        </FormItem>
    )
})

PictureField.displayName = 'PictureField'

export default React.memo(PictureField)
