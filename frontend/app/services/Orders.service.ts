import { API_BASE_URL } from "../config";

export async function fetchOrdersAPI(page = 0) {
  const res = await fetch(`${API_BASE_URL}/api/v1/orders?page=${page}&size=10`, {
    credentials: "include",
  });
  if (!res.ok) throw new Error(await res.text());
  return res.json();
}

export async function fetchOrderAPI(id: number) {
  const res = await fetch(`${API_BASE_URL}/api/v1/orders/${id}`, {
    credentials: "include",
  });
  if (!res.ok) throw new Error(await res.text());
  return res.json();
}