<script setup>
import { ChevronDown, ChevronLeft, ChevronRight } from 'lucide-vue-next'
import BookCard from '@/components/BookCard.vue'
import { Card } from '@/components/ui/card'
import { Button } from '@/components/ui/button'
import { Input } from '@/components/ui/input'
import { Label } from '@/components/ui/label'
import {
  DropdownMenu,
  DropdownMenuContent,
  DropdownMenuItem,
  DropdownMenuTrigger,
} from '@/components/ui/dropdown-menu'
</script>

<template>
  <div class="books-view">
    <!-- 筛选条件栏 -->
    <Card class="filter-card">
      <div class="flex flex-wrap gap-4 p-6">
        <div class="flex flex-col gap-2">
          <Label for="category">分类</Label>
          <DropdownMenu>
            <DropdownMenuTrigger asChild>
              <Button variant="outline" class="w-48 justify-between">
                全部分类
                <ChevronDown class="ml-2 h-4 w-4" />
              </Button>
            </DropdownMenuTrigger>
            <DropdownMenuContent>
              <DropdownMenuItem>
                全部分类
              </DropdownMenuItem>
            </DropdownMenuContent>
          </DropdownMenu>
        </div>

        <div class="flex flex-col gap-2">
          <Label for="publisher">出版社</Label>
          <Input
            id="publisher"
            placeholder="出版社"
            class="w-48"
          />
        </div>

        <div class="flex flex-col gap-2">
          <Label for="publishYear">出版年份</Label>
          <Input
            id="publishYear"
            placeholder="选择年份 (YYYY)"
            type="number"
            min="1800"
            max="2099"
            class="w-48"
          />
        </div>

        <div class="flex flex-col gap-2">
          <Label for="sortBy">排序方式</Label>
          <DropdownMenu>
            <DropdownMenuTrigger asChild>
              <Button variant="outline" class="w-48 justify-between">
                按借阅量
                <ChevronDown class="ml-2 h-4 w-4" />
              </Button>
            </DropdownMenuTrigger>
            <DropdownMenuContent>
              <DropdownMenuItem>
                按借阅量
              </DropdownMenuItem>
              <DropdownMenuItem>
                按评分
              </DropdownMenuItem>
              <DropdownMenuItem>
                按最新
              </DropdownMenuItem>
            </DropdownMenuContent>
          </DropdownMenu>
        </div>

        <div class="flex flex-col gap-2 justify-end">
          <Button class="w-20">筛选</Button>
        </div>
      </div>
    </Card>

    <!-- 图书列表 -->
    <div class="book-list">
      <BookCard />
    </div>

    <!-- 分页 -->
    <div class="flex justify-center items-center gap-2 mt-8">
      <Button variant="outline">
        <ChevronLeft class="h-4 w-4" />
        上一页
      </Button>
      
      <span class="text-sm text-muted-foreground">
        第 1 页，共 1 页
      </span>
      
      <Button variant="outline">
        下一页
        <ChevronRight class="h-4 w-4" />
      </Button>
    </div>
  </div>
</template>
