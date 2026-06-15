export interface GoodsInterface {
  userId: number
  id: number
  title: string
  detail: string
}

export interface SelectGoodsDataInterface {
  title: string
  detail: string
  page: number
  pageSize: number
  count: number
}

export class InitData {
  selectData: SelectGoodsDataInterface = {
    title: '',
    detail: '',
    page: 1,
    pageSize: 6,
    count: 0
  }
  list: GoodsInterface[] = []
}
