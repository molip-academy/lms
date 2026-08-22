/** back 의 DTO 와 1:1 로 대응한다. 한쪽만 바꾸지 않는다. */

export type Role = "USER" | "ADMIN"

export interface Member {
  id: number
  createDate: string
  modifyDate: string
  username: string
  nickname: string
  role: Role
}

/** 글/댓글에 붙는 작성자 표기. username 은 노출되지 않는다. */
export interface Author {
  id: number
  nickname: string
}

export interface PostListItem {
  id: number
  createDate: string
  modifyDate: string
  title: string
  author: Author
  viewCount: number
  likeCount: number
  commentCount: number
  liked: boolean
}

export interface Post {
  id: number
  createDate: string
  modifyDate: string
  title: string
  content: string
  author: Author
  viewCount: number
  likeCount: number
  liked: boolean
  canModify: boolean
  canDelete: boolean
}

export interface Comment {
  id: number
  createDate: string
  modifyDate: string
  content: string
  author: Author
  likeCount: number
  liked: boolean
  canModify: boolean
  canDelete: boolean
}

export interface PageResult<T> {
  content: T[]
  page: number
  size: number
  totalElements: number
  totalPages: number
}

export interface LikeResult {
  likeCount: number
  liked: boolean
}
