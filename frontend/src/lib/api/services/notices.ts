import { apiClient } from '../client'
import type {
  MessageResponse,
  Notice,
  NoticeCreateDto,
  NoticeUpdateDto,
  PagedResponse,
} from '../types'

export class NoticesService {
  private basePath = '/notices'

  async getAll(params?: { page?: number; size?: number }): Promise<PagedResponse<Notice>> {
    return apiClient.get<PagedResponse<Notice>>(this.basePath, params)
  }

  async getById(id: number): Promise<Notice> {
    return apiClient.get<Notice>(`${this.basePath}/${id}`)
  }

  async create(noticeData: NoticeCreateDto): Promise<MessageResponse> {
    return apiClient.post<MessageResponse>(this.basePath, noticeData)
  }

  async update(id: number, noticeData: NoticeUpdateDto): Promise<MessageResponse> {
    return apiClient.put<MessageResponse>(`${this.basePath}/${id}`, noticeData)
  }

  async delete(id: number): Promise<MessageResponse> {
    return apiClient.delete<MessageResponse>(`${this.basePath}/${id}`)
  }
}

export const noticesService = new NoticesService()
