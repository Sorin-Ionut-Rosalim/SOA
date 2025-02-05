import LoginForm from "@/feature/login/login-form";

export default function Login() {
  return (
    <div
      className="w-screen justify-center flex items-center  min-h-screen font-[family-name:var(--font-geist-sans)] bg-gradient-to-l from-[#fb7185] via-[#a21caf] to-[#6366f1]
        dark:bg-gradient-to-bl dark:from-[#0f172a] dark:via-[#1e1a78] dark:to-[#0f172a]"
    >
      <LoginForm />
    </div>
  );
}
