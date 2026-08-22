/**
 * ADR 0001: 이 Worker 가 `/api/*` 를 Railway 로 넘겨 front 와 API 를 같은 origin 으로 만든다.
 *
 * 인증 cookie 가 first-party 가 되는 것이 이 구조의 전부다. proxy 를 걷어내면
 * cookie 가 third-party 가 되어 Safari ITP 와 브라우저의 third-party cookie 차단에 걸리고,
 * 로그인이 조용히 깨진다.
 *
 * `Cookie` 와 `Set-Cookie` header 를 **변형 없이** 통과시켜야 한다. 여기 손대지 말 것.
 */

interface Env {
	ASSETS: Fetcher
	BACKEND_URL: string
}

export default {
	async fetch(request: Request, env: Env): Promise<Response> {
		const url = new URL(request.url)

		if (url.pathname.startsWith("/api/")) {
			const backend = new URL(env.BACKEND_URL)
			backend.pathname = url.pathname
			backend.search = url.search

			// Request 를 그대로 감싸면 method, headers(Cookie 포함), body 가 유지된다.
			const proxied = new Request(backend, request)
			// 원본 Host 를 그대로 보내면 Railway 가 라우팅하지 못한다.
			proxied.headers.set("Host", backend.host)

			// 응답도 그대로 돌려준다. Set-Cookie 가 여기서 브라우저까지 전달된다.
			return fetch(proxied)
		}

		return env.ASSETS.fetch(request)
	},
}
