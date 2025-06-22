import { apiClient } from '../client'
import type {
  BookCopy,
  BookCopyCreateDto,
  BookCopyUpdateDto,
  MessageResponse,
} from '../types'

export class BookCopiesService {
  private basePath = '/books'
  private copyBasePath = '/book-copies'

  async getCopiesByBookId(bookId: number): Promise<BookCopy[]> {
    return apiClient.get<BookCopy[]>(`${this.basePath}/${bookId}/copies`)
  }

  async createCopy(copyData: BookCopyCreateDto): Promise<BookCopy> {
    return apiClient.post<BookCopy>(this.copyBasePath, copyData)
  }

  async updateCopy(copyId: number, copyData: BookCopyUpdateDto): Promise<BookCopy> {
    return apiClient.put<BookCopy>(`${this.copyBasePath}/${copyId}`, copyData)
  }

  async deleteCopy(copyId: number): Promise<void> {
    return apiClient.delete<void>(`${this.copyBasePath}/${copyId}`)
  }

  async updateCopyStatus(copyId: number, status: string): Promise<BookCopy> {
    return apiClient.patch<BookCopy>(`${this.copyBasePath}/${copyId}/status`, { status })
  }

  async getCopyById(copyId: number): Promise<BookCopy> {
    return apiClient.get<BookCopy>(`${this.copyBasePath}/${copyId}`)
  }

  async getAllAvailableCopies(): Promise<BookCopy[]> {
    return apiClient.get<BookCopy[]>(`${this.copyBasePath}/available`)
  }

  // Helper methods
  getStatusColor(status: string): string {
    switch (status) {
      case 'AVAILABLE':
        return 'text-green-600'
      case 'BORROWED':
        return 'text-blue-600'
      case 'MAINTENANCE':
        return 'text-yellow-600'
      case 'SCRAPPED':
      case 'DISCARDED':
        return 'text-red-600'
      default:
        return 'text-gray-600'
    }
  }

  getStatusBadgeColor(status: string): string {
    switch (status) {
      case 'AVAILABLE':
        return 'bg-green-100 text-green-800'
      case 'BORROWED':
        return 'bg-blue-100 text-blue-800'
      case 'MAINTENANCE':
        return 'bg-yellow-100 text-yellow-800'
      case 'SCRAPPED':
      case 'DISCARDED':
        return 'bg-red-100 text-red-800'
      default:
        return 'bg-gray-100 text-gray-800'
    }
  }

  formatStatus(status: string): string {
    switch (status) {
      case 'AVAILABLE':
        return 'Available'
      case 'BORROWED':
        return 'Borrowed'
      case 'MAINTENANCE':
        return 'Under Maintenance'
      case 'SCRAPPED':
        return 'Scrapped'
      case 'DISCARDED':
        return 'Discarded'
      default:
        return status
    }
  }
}

export const bookCopiesService = new BookCopiesService()