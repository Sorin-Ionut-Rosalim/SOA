import {
    Sidebar,
    SidebarContent,
    SidebarFooter,
    SidebarGroup,
    SidebarGroupContent,
    SidebarGroupLabel,
    SidebarHeader,
    SidebarMenu,
    SidebarMenuButton,
    SidebarMenuItem,
    SidebarRail,
    SidebarSeparator,
} from '@/components/ui/sidebar'
import { cn } from '@/lib/utils'
import Link from 'next/link'
import { usePathname } from 'next/navigation'
import { IoHome, IoNotifications, IoPerson } from 'react-icons/io5'
import { FaSquarePlus } from 'react-icons/fa6'
import { useAuthStore } from '@/store/auth.store'
import { Avatar, AvatarFallback, AvatarImage } from '@/components/ui/avatar'
import { Button } from '@/components/ui/button'
import { useLogoutMutation } from '@/hooks/mutations/identity'
import { useNotifications } from '@/providers/notifications-provider'
// Menu items.
const items = [
    {
        title: 'Home',
        url: '/home',
        icon: IoHome,
    },
    {
        title: 'Notifications',
        url: '/home/notifications',
        icon: IoNotifications,
    },
    {
        title: 'Profile',
        url: '/home/profile',
        icon: IoPerson,
    },
    {
        title: 'New Post',
        url: '/home/post',
        icon: FaSquarePlus,
    },
]

export function AppSidebar() {
    const pathname = usePathname()
    const user = useAuthStore((store) => store.user)
    const { unread } = useNotifications()
    const { mutate: logout } = useLogoutMutation()
    return (
        <Sidebar collapsible="icon" variant="sidebar">
            <SidebarHeader className="p-4 py-4 group-data-[collapsible=icon]:p-2">
                <div className="flex items-center gap-5">
                    <Avatar className="transition-all ease-in-out group-data-[collapsible=icon]:size-8">
                        <AvatarImage src={user?.profilePic} />
                        <AvatarFallback>{user?.username}</AvatarFallback>
                    </Avatar>
                    <span className="font-bold text-black group-data-[collapsible=icon]:hidden dark:text-white">
                        {user?.username}
                    </span>
                </div>
            </SidebarHeader>
            <SidebarSeparator />

            <SidebarContent>
                <SidebarGroup className="flex-1">
                    <SidebarGroupLabel>Notagram</SidebarGroupLabel>
                    <SidebarGroupContent>
                        <SidebarMenu className="gap-1 pt-1 group-data-[collapsible=icon]:gap-2 group-data-[collapsible=icon]:pt-2">
                            {items.map((item) => (
                                <SidebarMenuItem key={item.title}>
                                    <SidebarMenuButton
                                        asChild
                                        size={'lg'}
                                        className="gap-4 [&>svg]:size-6"
                                    >
                                        <Link
                                            href={
                                                item.title === 'Profile'
                                                    ? `${item.url}/${user?.username}`
                                                    : item.url
                                            }
                                            className="relative"
                                        >
                                            <item.icon
                                                className={cn(
                                                    pathname === item.url &&
                                                        'text-fuchsia-600 dark:text-blue-800'
                                                )}
                                            />
                                            <span className="text-2xl">
                                                {item.title}
                                            </span>
                                            {/* If this is the Notifications item AND we have an unread notification, show a red badge */}
                                            {item.title === 'Notifications' &&
                                                unread && (
                                                    <span className="absolute right-8 top-1 inline-flex size-3 animate-pulse rounded-full bg-red-700 dark:bg-red-500" />
                                                )}
                                        </Link>
                                    </SidebarMenuButton>
                                </SidebarMenuItem>
                            ))}
                        </SidebarMenu>
                    </SidebarGroupContent>
                </SidebarGroup>

                <SidebarFooter className="p-10">
                    <Button type="button" onClick={() => logout()}>
                        Logout
                    </Button>
                </SidebarFooter>
            </SidebarContent>
            <SidebarRail />
        </Sidebar>
    )
}
