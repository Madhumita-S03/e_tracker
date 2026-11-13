export interface Category {
  id: number;
  name: string;
  description?: string;
  colorCode?: string;
  icon?: string;
  defaultCategory: boolean;
}

export interface CategoryRequest {
  name: string;
  description?: string;
  colorCode?: string;
  icon?: string;
}
