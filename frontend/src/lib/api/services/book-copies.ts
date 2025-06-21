import { apiClient } from '../client'
import type {
  BookCopy,
  BookCopyCreateDto,
  BookCopyUpdateDto,
  MessageResponse,
} from '../types'

export class BookCopiesService {
  private basePath = '/books'

  async getCopiesByBookId(bookId: number): Promise<BookCopy[]> {
    return apiClient.get<BookCopy[]>(`${this.basePath}/${bookId}/copies`)
  }

  // Future endpoints when backend supports them
  async createCopy(copyData: BookCopyCreateDto): Promise<MessageResponse> {
    // This would be implemented when backend supports it
    // return apiClient.post<MessageResponse>('/book-copies', copyData)
    throw new Error('Creating book copies is not yet supported by the backend')
  }

  async updateCopy(copyId: number, copyData: BookCopyUpdateDto): Promise<MessageResponse> {
    // This would be implemented when backend supports it
    // return apiClient.put<MessageResponse>(`/book-copies/${copyId}`, copyData)
    throw new Error('Updating book copies is not yet supported by the backend')
  }

  async deleteCopy(copyId: number): Promise<void> {
    // This would be implemented when backend supports it
    // return apiClient.delete<void>(`/book-copies/${copyId}`)
    throw new Error('Deleting book copies is not yet supported by the backend')
  }

  async updateCopyStatus(copyId: number, status: string): Promise<MessageResponse> {
    // This would be implemented when backend supports it
    // return apiClient.patch<MessageResponse>(`/book-copies/${copyId}/status`, { status })
    throw new Error('Updating book copy status is not yet supported by the backend')
  }

  async getCopyById(copyId: number): Promise<BookCopy> {
    // This would be implemented when backend supports it
    // return apiClient.get<BookCopy>(`/book-copies/${copyId}`)
    throw new Error('Getting individual book copies is not yet supported by the backend')
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