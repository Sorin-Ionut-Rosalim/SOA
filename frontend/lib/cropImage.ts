// /lib/cropImage.ts

export const getCroppedImg = (
    imageSrc: string,
    pixelCrop: { x: number; y: number; width: number; height: number },
    outputWidth: number,
    outputHeight: number
): Promise<string> => {
    return new Promise((resolve, reject) => {
        const image = new Image()
        image.src = imageSrc
        image.onload = () => {
            const canvas = document.createElement('canvas')
            canvas.width = outputWidth
            canvas.height = outputHeight
            const ctx = canvas.getContext('2d')
            if (!ctx) {
                return reject(new Error('Could not get canvas context'))
            }
            // Draw the cropped region of the image on the canvas and scale to output dimensions.
            ctx.drawImage(
                image,
                pixelCrop.x,
                pixelCrop.y,
                pixelCrop.width,
                pixelCrop.height,
                0,
                0,
                outputWidth,
                outputHeight
            )
            // Convert the canvas to a base64-encoded data URL.
            resolve(canvas.toDataURL('image/jpeg'))
        }
        image.onerror = (error) => {
            reject(error)
        }
    })
}
