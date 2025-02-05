"use client";

import PasswordField from "@/components/form-fields/password-field";
import UsernameField from "@/components/form-fields/username-field";
import { Button } from "@/components/ui/button";
import { Form, FormField } from "@/components/ui/form";
import { Separator } from "@/components/ui/separator";
import Link from "next/link";
import { LoginUser, loginUserSchema } from "@/types/identity";
import { zodResolver } from "@hookform/resolvers/zod";
import { useForm } from "react-hook-form";
import { useLoginMutation } from "@/hooks/mutations/identity";

export default function LoginForm() {
  const form = useForm<LoginUser>({
    resolver: zodResolver(loginUserSchema),
    defaultValues: {
      password: "",
      username: "",
    },
  });
  const { mutate: login } = useLoginMutation();

  function onSubmit(values: LoginUser) {
    login(values);
  }

  return (
    <Form {...form}>
      <form
        className="space-y-8 dark:bg-zinc-800 dark:bg-opacity-80 bg-zinc-200 bg-opacity-80 w-[500px] p-10 rounded-xl"
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

        <Button type="submit" className="w-full" size={"lg"}>
          Login
        </Button>
        <Separator className="bg-zinc-900 h-[2px] dark:bg-white" />
        <div className="text-center">
          <Link
            href="/register"
            className="text-sm font-semibold text-blue-500  hover:underline"
          >
            Don’t have an account? Sign up now!
          </Link>
        </div>
      </form>
    </Form>
  );
}
