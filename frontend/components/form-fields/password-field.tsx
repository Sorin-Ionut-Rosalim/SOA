import { RegisterUser } from "@/types/identity";
import React, { forwardRef } from "react";
import { ControllerRenderProps } from "react-hook-form";
import {
  FormControl,
  FormItem,
  FormLabel,
  FormMessage,
} from "@/components/ui/form";
import { Input } from "@/components/ui/input";

const PasswordField = forwardRef<
  HTMLInputElement,
  ControllerRenderProps<RegisterUser, "password">
>((field, ref) => {
  return (
    <FormItem className="w-full">
      <FormLabel>Password</FormLabel>
      <FormControl>
        <Input
          placeholder=""
          autoComplete="current-password"
          type="password"
          {...field}
          ref={ref}
        />
      </FormControl>
      <FormMessage />
    </FormItem>
  );
});

PasswordField.displayName = "PasswordField";
export default React.memo(PasswordField);
