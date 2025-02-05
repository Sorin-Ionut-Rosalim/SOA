import { z } from 'zod'

const notificationType = z.enum(['NEW_POST', 'FOLLOW', 'LIKE'] as const)
type NotificationType = z.infer<typeof notificationType>

const newPostNotification = z.object({
    type: z.literal(notificationType.Enum.NEW_POST),
    postId: z.number(),
    username: z.string(),
    timestamp: z.number(),
})

const followNotification = z.object({
    type: z.literal(notificationType.Enum.FOLLOW),
    follower: z.string(),
    followee: z.string(),
    action: z.enum(['FOLLOW', 'UNFOLLOW'] as const),
})

const likeNotification = z.object({
    type: z.literal(notificationType.Enum.LIKE),
    username: z.string(),
    postId: z.number(),
    action: z.enum(['LIKE', 'DISLIKE'] as const),
})

type NewPostNotification = z.infer<typeof newPostNotification>

const notificationContent = z.discriminatedUnion(
    'type',
    [newPostNotification, followNotification, likeNotification],
    {
        errorMap: (issue, ctx) => {
            if (issue.code === z.ZodIssueCode.invalid_union_discriminator) {
                return { message: 'Invalid type' }
            }
            return { message: ctx.defaultError }
        },
    }
)
type NotificationContent = z.infer<typeof notificationContent>

export type { NotificationType, NewPostNotification, NotificationContent }
export { notificationType, newPostNotification, notificationContent }
