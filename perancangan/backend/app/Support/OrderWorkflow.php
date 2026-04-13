<?php

namespace App\Support;

class OrderWorkflow
{
    public const FULL = 'FULL_LAUNDRY';
    public const IRON_ONLY = 'SETRIKA_SAJA';

    /**
     * @return array<int, string>
     */
    public static function flow(string $serviceCode): array
    {
        return match ($serviceCode) {
            self::IRON_ONLY => ['DITERIMA', 'DISETRIKA', 'SELESAI', 'DIAMBIL'],
            default => ['DITERIMA', 'DICUCI', 'DIJEMUR', 'DISETRIKA', 'SELESAI', 'DIAMBIL'],
        };
    }

    public static function canTransition(string $serviceCode, string $currentStatus, string $nextStatus): bool
    {
        $flow = self::flow($serviceCode);
        $currentIndex = array_search($currentStatus, $flow, true);

        if ($currentIndex === false) {
            return false;
        }

        return isset($flow[$currentIndex + 1]) && $flow[$currentIndex + 1] === $nextStatus;
    }
}
