# API 명세

## 공통

- Base URL: `/api/v1`
- 로그인 API를 제외한 API는 인증이 필요합니다.
- 권한이 표시된 API는 해당 스토어의 `MEMBER` 또는 `MANAGER` 권한이 필요합니다. `MANAGER`는 `MEMBER` 권한 API도 호출할 수 있습니다.
- Request Body가 있는 API는 `Content-Type: application/json`을 사용합니다.
- 생성 API는 `201 Created`, 조회 및 변경/삭제 API는 `200 OK`를 반환합니다.
- 응답 본문이 표기되지 않은 변경/삭제 API의 성공 응답에는 본문이 없습니다.

**Error Response Body**

```json
{
  "code": "INVENTORY_NOT_FOUND",
  "message": "재고를 찾을 수 없습니다. (Inventory ID : 1)"
}
```

| 상태 코드 | 발생 조건 |
|-----------|-----------|
| `400 Bad Request` | 요청 필드 검증 실패, 필수 파라미터 누락, 타입 변환 실패 |
| `401 Unauthorized` | 인증 실패, 로그인 필요 |
| `403 Forbidden` | 스토어 권한 부족 |
| `404 Not Found` | 조회 대상 없음 |
| `409 Conflict` | 도메인 규칙 위반 |
| `500 Internal Server Error` | 처리되지 않은 서버 오류 |

---

## Auth

| 메서드 | 경로 | 설명 | 인증 | 성공 상태 |
|--------|------|------|------|-----------|
| POST | `/api/v1/auth/{socialType}/login` | 소셜 로그인 | 불필요 | `200 OK` |

**Path Variables**

| 변수 | 값 |
|------|----|
| `socialType` | `GOOGLE`, `KAKAO` (대소문자 구분 없이 입력 가능) |

**Request Body**

```json
{
  "code": "authorization_code"
}
```

**Response Body**

```json
{
  "accessToken": "...",
  "refreshToken": "..."
}
```

`accessToken`, `refreshToken`은 응답 본문과 함께 HttpOnly 쿠키로도 발급됩니다.

---

## Store

| 메서드 | 경로 | 설명 | 필요 권한 | 성공 상태 |
|--------|------|------|-----------|-----------|
| POST | `/api/v1/stores` | 스토어 생성 | 로그인 | `201 Created` |
| PUT | `/api/v1/stores/{storeId}` | 스토어 정보 수정 | `MANAGER` | `200 OK` |
| POST | `/api/v1/stores/{storeId}/members` | 멤버 추가 | `MANAGER` | `200 OK` |
| DELETE | `/api/v1/stores/{storeId}/members/{userId}` | 멤버 삭제 | `MANAGER` | `200 OK` |
| PATCH | `/api/v1/stores/{storeId}/members/{userId}/manager` | 매니저 권한 부여 | `MANAGER` | `200 OK` |
| PATCH | `/api/v1/stores/{storeId}/members/{userId}/member` | 멤버 권한 부여 | `MANAGER` | `200 OK` |

**스토어 생성 Request Body**

```json
{
  "name": "스토어명"
}
```

| 필드 | 타입 | 필수 | 제약 |
|------|------|------|------|
| `name` | `string` | O | 공백이 아닌 값 |

**스토어 수정 Request Body**

```json
{
  "name": "수정된 스토어명"
}
```

| 필드 | 타입 | 필수 | 제약 |
|------|------|------|------|
| `name` | `string` | O | 공백이 아닌 값 |

**멤버 추가 Request Body**

```json
{
  "userId": "550e8400-e29b-41d4-a716-446655440000"
}
```

| 필드 | 타입 | 필수 |
|------|------|------|
| `userId` | `UUID` | O |

---

## User Store

| 메서드 | 경로 | 설명 | 필요 권한 | 성공 상태 |
|--------|------|------|-----------|-----------|
| GET | `/api/v1/users/stores` | 내 스토어 목록 조회 | 로그인 | `200 OK` |

**Response Body**

```json
[
  {
    "storeId": 1,
    "storeName": "스토어A"
  }
]
```

---

## Place

| 메서드 | 경로 | 설명 | 필요 권한 | 성공 상태 |
|--------|------|------|-----------|-----------|
| POST | `/api/v1/places` | 장소 생성 | `MEMBER` | `201 Created` |
| PUT | `/api/v1/places/{placeId}` | 장소 수정 | `MEMBER` | `200 OK` |
| DELETE | `/api/v1/places/{placeId}` | 장소 삭제 | `MEMBER` | `200 OK` |
| GET | `/api/v1/places?storeId={storeId}` | 스토어의 장소 목록 조회 | `MEMBER` | `200 OK` |

**장소 생성 Request Body**

```json
{
  "storeId": 1,
  "name": "창고 A"
}
```

| 필드 | 타입 | 필수 | 제약 |
|------|------|------|------|
| `storeId` | `number` | O |  |
| `name` | `string` | O | 공백이 아닌 값 |

**장소 수정 Request Body**

```json
{
  "name": "창고 B"
}
```

| 필드 | 타입 | 필수 | 제약 |
|------|------|------|------|
| `name` | `string` | O | 공백이 아닌 값 |

**장소 목록 Query Params**

| 파라미터 | 타입 | 필수 | 설명 |
|----------|------|------|------|
| `storeId` | `number` | O | 조회할 스토어 ID |

**장소 목록 Response Body**

```json
[
  {
    "id": 1,
    "name": "창고 A"
  }
]
```

---

## Product

| 메서드 | 경로 | 설명 | 필요 권한 | 성공 상태 |
|--------|------|------|-----------|-----------|
| POST | `/api/v1/products` | 상품 등록 | `MEMBER` | `201 Created` |
| PUT | `/api/v1/products/{productId}` | 상품 수정 | `MEMBER` | `200 OK` |
| PATCH | `/api/v1/products/{productId}/stop` | 상품 판매 중지 | `MEMBER` | `200 OK` |
| PATCH | `/api/v1/products/{productId}/register` | 상품 재등록 | `MEMBER` | `200 OK` |
| DELETE | `/api/v1/products/{productId}` | 상품 삭제 | `MEMBER` | `200 OK` |
| GET | `/api/v1/products/{productId}` | 상품 상세 조회 | `MEMBER` | `200 OK` |
| GET | `/api/v1/products` | 상품 목록 검색 | `MEMBER` | `200 OK` |

**상품 등록 Request Body**

```json
{
  "storeId": 1,
  "name": "상품명",
  "sku": "SKU-001",
  "barcode": "BARCODE-001",
  "imageUrl": "https://example.com/image.png",
  "categoryIds": [1, 2]
}
```

| 필드 | 타입 | 필수 | 제약 |
|------|------|------|------|
| `storeId` | `number` | O |  |
| `name` | `string` | O | 공백이 아닌 값 |
| `sku` | `string` | O | 공백이 아닌 값 |
| `barcode` | `string` | X |  |
| `imageUrl` | `string` | X |  |
| `categoryIds` | `number[]` | X |  |

**상품 수정 Request Body**

```json
{
  "name": "상품명",
  "sku": "SKU-001",
  "barcode": "BARCODE-001",
  "imageUrl": "https://example.com/image.png",
  "categoryIds": [1, 2]
}
```

| 필드 | 타입 | 필수 | 제약 |
|------|------|------|------|
| `name` | `string` | O | 공백이 아닌 값 |
| `sku` | `string` | O | 공백이 아닌 값 |
| `barcode` | `string` | X |  |
| `imageUrl` | `string` | X |  |
| `categoryIds` | `number[]` | X |  |

**상품 목록 검색 Query Params**

| 파라미터 | 타입 | 필수 | 설명 |
|----------|------|------|------|
| `storeId` | `number` | O | 스토어 ID |
| `keyword` | `string` | X | 검색 키워드 |
| `status` | `string` | X | `REGISTERED`, `STOPPED`, `DELETED`; 기본값 `REGISTERED` |
| `page` | `number` | X | 1 이상; 기본값 `1` |
| `size` | `number` | X | 1 이상; 기본값 `10` |

**상품 상세 Response Body**

```json
{
  "id": 1,
  "name": "상품명",
  "sku": "SKU-001",
  "barcode": "BARCODE-001",
  "imageUrl": "https://example.com/image.png",
  "status": {
    "code": "REGISTERED",
    "desc": "등록"
  },
  "categories": [
    {
      "id": 1,
      "name": "카테고리명"
    }
  ]
}
```

**상품 목록 검색 Response Body**

```json
{
  "content": [
    {
      "id": 1,
      "name": "상품명",
      "sku": "SKU-001",
      "barcode": "BARCODE-001",
      "imageUrl": "https://example.com/image.png",
      "status": {
        "code": "REGISTERED",
        "desc": "등록"
      },
      "categories": [
        {
          "id": 1,
          "name": "카테고리명"
        }
      ]
    }
  ],
  "page": 1,
  "size": 10,
  "totalPage": 1,
  "totalCount": 1
}
```

---

## Inventory

재고는 장소별 상품 수량입니다. 모든 재고 API는 경로의 `placeId`에 대한 `MEMBER` 권한을 요구합니다.

| 메서드 | 경로 | 설명 | 필요 권한 | 성공 상태 |
|--------|------|------|-----------|-----------|
| POST | `/api/v1/places/{placeId}/inventories` | 장소에 상품 재고 등록 | `MEMBER` | `201 Created` |
| GET | `/api/v1/places/{placeId}/inventories` | 장소의 재고 목록 조회 | `MEMBER` | `200 OK` |
| PATCH | `/api/v1/places/{placeId}/inventories/{inventoryId}/inbound` | 입고 처리 | `MEMBER` | `200 OK` |
| PATCH | `/api/v1/places/{placeId}/inventories/{inventoryId}/outbound` | 출고 처리 | `MEMBER` | `200 OK` |
| PATCH | `/api/v1/places/{placeId}/inventories/{inventoryId}/adjust` | 실사 수량 조정 | `MEMBER` | `200 OK` |
| PATCH | `/api/v1/places/{placeId}/inventories/{inventoryId}/transfer` | 다른 장소로 재고 이동 | `MEMBER` | `200 OK` |
| DELETE | `/api/v1/places/{placeId}/inventories/{inventoryId}` | 재고 삭제 | `MEMBER` | `200 OK` |

**재고 등록 Request Body**

```json
{
  "productId": 1
}
```

| 필드 | 타입 | 필수 | 설명 |
|------|------|------|------|
| `productId` | `number` | O | 장소와 동일한 스토어에 등록된 상품 ID |

등록된 재고의 초기 수량은 `0`입니다. 한 장소에는 동일 상품의 재고를 중복 등록할 수 없습니다.

**재고 목록 Response Body**

```json
[
  {
    "id": 1,
    "productId": 1,
    "productName": "상품명",
    "quantity": 10
  }
]
```

**입고 Request Body**

```json
{
  "quantity": 10
}
```

**출고 Request Body**

```json
{
  "quantity": 3
}
```

| 필드 | 타입 | 필수 | 제약 |
|------|------|------|------|
| `quantity` | `number` | O | 1 이상 |

출고 수량은 현재 재고 수량을 초과할 수 없습니다.

**수량 조정 Request Body**

```json
{
  "newQuantity": 8,
  "reason": "정기 실사"
}
```

| 필드 | 타입 | 필수 | 제약 |
|------|------|------|------|
| `newQuantity` | `number` | X | 0 이상; 생략 시 `0`으로 처리됨 |
| `reason` | `string` | X | 조정 사유 |

**재고 이동 Request Body**

```json
{
  "toPlaceId": 2,
  "quantity": 5
}
```

| 필드 | 타입 | 필수 | 제약 |
|------|------|------|------|
| `toPlaceId` | `number` | O | 원본 장소와 동일한 스토어의 목적지 장소 ID |
| `quantity` | `number` | O | 1 이상, 현재 수량 이하 |

목적지 장소에 해당 상품의 재고가 없으면 이동 처리 중 수량 `0`인 재고가 생성된 뒤 이동 수량이 입고됩니다.

**재고 API Business Errors**

| 코드 | 상태 | 발생 조건 |
|------|------|-----------|
| `PLACE_NOT_FOUND` | `404 Not Found` | 장소가 존재하지 않음 |
| `PRODUCT_NOT_FOUND` | `404 Not Found` | 상품이 존재하지 않음 |
| `INVENTORY_NOT_FOUND` | `404 Not Found` | 경로의 장소에 해당 재고가 존재하지 않음 |
| `INVENTORY_DUPLICATE` | `409 Conflict` | 같은 장소에 같은 상품 재고를 다시 등록함 |
| `PRODUCT_NOT_IN_STORE` | `409 Conflict` | 재고 등록 상품이 장소의 스토어 소속이 아님 |
| `INSUFFICIENT_STOCK` | `409 Conflict` | 출고 또는 이동 수량이 현재 수량을 초과함 |
| `PLACE_NOT_IN_SAME_STORE` | `409 Conflict` | 이동 대상 장소가 다른 스토어 소속임 |
| `INVENTORY_HAS_STOCK` | `409 Conflict` | 수량이 0이 아닌 재고를 삭제하려 함 |
