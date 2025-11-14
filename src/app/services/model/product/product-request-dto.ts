export interface ProductRequestDto {
    name: string;
    description: string;
    price: number;        // BigDecimal in backend → number in frontend
    sku: string;
    productStock: number;
    warehouseName?: string;
    imageUrl?: string;
    status: string;       // can be enum-like string (e.g., "ACTIVE", "INACTIVE")
    categoryId: string;
    brandId: string;
}
