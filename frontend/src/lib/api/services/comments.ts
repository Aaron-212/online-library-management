import { apiClient } from '../client'
import type { 
  Comment, 
  CommentCreateDto, 
  CommentUpdateDto,
  PagedResponse,
  MessageResponse 
} from '../types'

export class CommentsService {
  private basePath = '/comments'

  async getByBookId(bookId: number, params?: { page?: number; size?: number }): Promise<PagedResponse<Comment>> {
    return apiClient.get<PagedResponse<Comment>>(`${this.basePath}/book/${bookId}`, params)
  }

  async getByUserId(userId: number, params?: { page?: number; size?: number }): Promise<PagedResponse<Comment>> {
    return apiClient.get<PagedResponse<Comment>>(`${this.basePath}/user/${userId}`, params)
  }

  async getCurrentUserComments(params?: { page?: number; size?: number }): Promise<PagedResponse<Comment>> {
    return apiClient.get<PagedResponse<Comment>>(`${this.basePath}/me`, params)
  }

  async getById(id: number): Promise<Comment> {
    return apiClient.get<Comment>(`${this.basePath}/${id}`)
  }

  async create(commentData: CommentCreateDto): Promise<MessageResponse> {
    return apiClient.post<MessageResponse>(this.basePath, commentData)
  }

  async update(id: number, commentData: CommentUpdateDto): Promise<MessageResponse> {
    return apiClient.put<MessageResponse>(`${this.basePath}/${id}`, commentData)
  }

  async delete(id: number): Promise<MessageResponse> {
    return apiClient.delete<MessageResponse>(`${this.basePath}/${id}`)
  }
}

export const commentsService = new CommentsService()