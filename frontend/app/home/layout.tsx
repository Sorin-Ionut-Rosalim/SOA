'use client'
import { SidebarProvider } from '@/components/ui/sidebar'
import { AppSidebar } from '@/feature/sidebar/app-sidebar'
import { useSessionQuery } from '@/hooks/queries/identity'
import { NotificationsProvider } from '@/providers/notifications-provider'

export default function HomeLayout({
    children,
}: Readonly<{
    children: React.ReactNode
}>) {
    const sessionQuery = useSessionQuery()

    if (
        sessionQuery.isLoading ||
        sessionQuery.isPending ||
        sessionQuery.isFetching
    ) {
        return <div>loading...</div>
    }
    if (sessionQuery.isError) {
        return <div>{sessionQuery.error.message}</div>
    }

    return (
        <div className="flex min-h-screen overflow-x-hidden bg-gradient-to-l from-[#fb7185] via-[#a21caf] to-[#6366f1] font-[family-name:var(--font-geist-sans)] dark:bg-gradient-to-bl dark:from-[#0f172a] dark:via-[#1e1a78] dark:to-[#0f172a]">
            <SidebarProvider>
                <NotificationsProvider>
                    <AppSidebar />
                    <main className="flex-1">{children}</main>
                </NotificationsProvider>
            </SidebarProvider>
        </div>
    )
}
