import { z } from "zod";

const userSchema = z.object({
  username: z.string().min(1),
  profilePic: z.string().url(),
});

type User = z.infer<typeof userSchema>;

const loginUserSchema = z.object({
  username: z.string().min(1),
  password: z.string().min(1),
});

type LoginUser = z.infer<typeof loginUserSchema>;

const registerUserSchema = z.object({
  username: z.string().min(1),
  password: z.string().min(1),
});

type RegisterUser = z.infer<typeof registerUserSchema>;

export { userSchema, loginUserSchema, registerUserSchema };
export type { User, LoginUser, RegisterUser };
