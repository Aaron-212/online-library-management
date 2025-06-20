import { apiClient } from '../client'
import type {
  BorrowingRule,
  BorrowingRuleDto,
  BorrowingRuleUpdateDto,
  MessageResponse,
  PagedResponse,
} from '../types'

export class BorrowingRulesService {
  private basePath = '/borrowing-rules'

  async getAll(params?: { page?: number; size?: number }): Promise<PagedResponse<BorrowingRule>> {
    return apiClient.get<PagedResponse<BorrowingRule>>(this.basePath, params)
  }

  async getById(id: number): Promise<BorrowingRule> {
    return apiClient.get<BorrowingRule>(`${this.basePath}/${id}`)
  }

  async getByName(name: string): Promise<BorrowingRule> {
    return apiClient.get<BorrowingRule>(`${this.basePath}/name/${name}`)
  }

  async create(ruleData: BorrowingRuleDto): Promise<MessageResponse> {
    return apiClient.post<MessageResponse>(this.basePath, ruleData)
  }

  async update(id: number, ruleData: BorrowingRuleUpdateDto): Promise<MessageResponse> {
    return apiClient.put<MessageResponse>(`${this.basePath}/${id}`, ruleData)
  }

  async delete(id: number): Promise<MessageResponse> {
    return apiClient.delete<MessageResponse>(`${this.basePath}/${id}`)
  }
}

export const borrowingRulesService = new BorrowingRulesService()
