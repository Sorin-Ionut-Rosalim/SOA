"use client";
import { useTheme } from "next-themes";
import { memo } from "react";
import { ToastContainer } from "react-toastify";

const ToastContainerWithTheme = memo(() => {
  const { theme } = useTheme();
  return <ToastContainer aria-label={"toast container"} theme={theme} />;
});
ToastContainerWithTheme.displayName = "ToastContainerWithTheme";
export default ToastContainerWithTheme;
