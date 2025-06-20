import { apiClient } from '../client'
import type { IndexCategory } from '../types'

export class CategoriesService {
  private basePath = '/categories'

  async getAll(): Promise<IndexCategory[]> {
    return apiClient.get<IndexCategory[]>(this.basePath)
  }

  async getByIndexCode(indexCode: string): Promise<IndexCategory> {
    return apiClient.get<IndexCategory>(`${this.basePath}/${indexCode}`)
  }

  async exists(indexCode: string): Promise<boolean> {
    return apiClient.get<boolean>(`${this.basePath}/${indexCode}/exists`)
  }

  async create(indexCode: string): Promise<IndexCategory> {
    return apiClient.post<IndexCategory>(`${this.basePath}?indexCode=${indexCode}`)
  }
}

export const categoriesService = new CategoriesService() 
