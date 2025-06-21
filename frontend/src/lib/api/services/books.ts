import { apiClient } from '../client'
import type {
  Book,
  BookCopy,
  BookCreateDto,
  BookSearchParams,
  BookSummaryDto,
  BookUpdateDto,
  MessageResponse,
  PagedResponse,
} from '../types'

export class BooksService {
  private basePath = '/books'

  async getAll(params?: {
    page?: number
    size?: number
    sort?: string
  }): Promise<PagedResponse<Book>> {
    return apiClient.get<PagedResponse<Book>>(this.basePath, params)
  }

  async getAllSummary(params?: {
    page?: number
    size?: number
    sort?: string
  }): Promise<PagedResponse<BookSummaryDto>> {
    return apiClient.get<PagedResponse<BookSummaryDto>>(`${this.basePath}/summary`, params)
  }

  async getById(id: number): Promise<Book> {
    return apiClient.get<Book>(`${this.basePath}/${id}`)
  }

  async getByIsbn(isbn: string): Promise<Book> {
    return apiClient.get<Book>(`${this.basePath}/isbn/${isbn}`)
  }

  async search(params: BookSearchParams): Promise<PagedResponse<Book>> {
    const { keyword, ...paginationParams } = params
    return apiClient.get<PagedResponse<Book>>(`${this.basePath}/search`, {
      keyword,
      ...paginationParams,
    })
  }

  async create(bookData: BookCreateDto): Promise<MessageResponse> {
    return apiClient.post<MessageResponse>(`${this.basePath}/create`, bookData)
  }

  async update(id: number, bookData: BookUpdateDto): Promise<MessageResponse> {
    return apiClient.put<MessageResponse>(`${this.basePath}/${id}`, bookData)
  }

  async delete(id: number): Promise<void> {
    return apiClient.delete<void>(`${this.basePath}/${id}`)
  }

  async checkExistsByIsbn(isbn: string): Promise<{ exists: boolean }> {
    return apiClient.get<{ exists: boolean }>(`${this.basePath}/exists/isbn/${isbn}`)
  }

  async getCopies(bookId: number): Promise<BookCopy[]> {
    return apiClient.get<BookCopy[]>(`${this.basePath}/${bookId}/copies`)
  }
}

export const booksService = new BooksService()
