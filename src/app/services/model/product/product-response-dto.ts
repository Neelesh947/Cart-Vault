import { ProductStatus } from "../../enums/product-status";

export interface ProductResponseDto {
    quantity: any;
    id: string;
    name: string;
    createdBy?: string;
    description: string;
    price: number;          // BigDecimal → number
    sku: string;
    imageUrl?: string;
    rating?: number;
    status: ProductStatus;

    categoryId?: string;
    categoryName?: string;

    brandId?: string;
    brandName?: string;
    productStock: number;
    warehouseName?: string;
}
