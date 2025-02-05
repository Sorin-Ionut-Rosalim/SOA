'use client'

import PasswordField from '@/components/form-fields/password-field'
import UsernameField from '@/components/form-fields/username-field'
import { Button } from '@/components/ui/button'
import { Form, FormField } from '@/components/ui/form'
import { Separator } from '@/components/ui/separator'
import Link from 'next/link'
import { RegisterUser, registerUserSchema } from '@/types/identity'
import { zodResolver } from '@hookform/resolvers/zod'
import { useForm } from 'react-hook-form'
import { useRegisterMutation } from '@/hooks/mutations/identity'

export default function RegisterForm() {
    const form = useForm<RegisterUser>({
        resolver: zodResolver(registerUserSchema),
        defaultValues: {
            password: '',
            username: '',
        },
    })

    const { mutate: register } = useRegisterMutation()

    function onSubmit(values: RegisterUser) {
        register(values)
    }

    return (
        <Form {...form}>
            <form
                className="w-[500px] space-y-8 rounded-xl bg-zinc-200 bg-opacity-80 p-10 dark:bg-zinc-800 dark:bg-opacity-80"
                onSubmit={form.handleSubmit(onSubmit)}
            >
                <FormField
                    control={form.control}
                    name="username"
                    render={({ field }) => <UsernameField {...field} />}
                />
                <FormField
                    control={form.control}
                    name="password"
                    render={({ field }) => <PasswordField {...field} />}
                />

                <Button type="submit" className="w-full" size={'lg'}>
                    Register
                </Button>
                <Separator className="h-[2px] bg-zinc-900 dark:bg-white" />
                <div className="text-center">
                    <Link
                        href="/login"
                        className="text-sm font-semibold text-blue-500 hover:underline"
                    >
                        Already have an account? Sign in
                    </Link>
                </div>
            </form>
        </Form>
    )
}
