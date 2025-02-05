import { User } from "@/types/identity";
import React, { forwardRef } from "react";
import { ControllerRenderProps } from "react-hook-form";
import {
  FormControl,
  FormItem,
  FormLabel,
  FormMessage,
} from "@/components/ui/form";
import { Input } from "@/components/ui/input";

const UsernameField = forwardRef<
  HTMLInputElement,
  ControllerRenderProps<User, "username">
>((field, ref) => {
  return (
    <FormItem className="w-full ">
      <FormLabel>Username</FormLabel>
      <FormControl>
        <Input autoComplete="username" placeholder="" {...field} ref={ref} />
      </FormControl>
      <FormMessage />
    </FormItem>
  );
});
UsernameField.displayName = "UsernameField";

export default React.memo(UsernameField);
