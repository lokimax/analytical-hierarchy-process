import { TestBed } from '@angular/core/testing';
import { LoadingService } from './loading.service';

describe('LoadingService', () => {
  let service: LoadingService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(LoadingService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  describe('Initial State', () => {
    it('should initialize with isLoading false', () => {
      expect(service.isLoading()).toBeFalse();
    });
  });

  describe('show', () => {
    it('should set isLoading to true', () => {
      service.show();
      
      expect(service.isLoading()).toBeTrue();
    });

    it('should increment loading count', () => {
      service.show();
      service.show();
      service.show();
      
      expect(service.isLoading()).toBeTrue();
    });

    it('should handle multiple concurrent loading operations', () => {
      // Simulate 5 concurrent operations
      for (let i = 0; i < 5; i++) {
        service.show();
      }
      
      expect(service.isLoading()).toBeTrue();
      
      // Complete 4 operations
      for (let i = 0; i < 4; i++) {
        service.hide();
      }
      
      // Should still be loading (1 operation remaining)
      expect(service.isLoading()).toBeTrue();
      
      // Complete last operation
      service.hide();
      expect(service.isLoading()).toBeFalse();
    });
  });

  describe('hide', () => {
    it('should set isLoading to false when count reaches zero', () => {
      service.show();
      expect(service.isLoading()).toBeTrue();
      
      service.hide();
      expect(service.isLoading()).toBeFalse();
    });

    it('should not go below zero', () => {
      service.hide();
      service.hide();
      service.hide();
      
      expect(service.isLoading()).toBeFalse();
    });

    it('should maintain true state until all operations complete', () => {
      service.show();
      service.show();
      service.show();
      
      service.hide();
      expect(service.isLoading()).toBeTrue();
      
      service.hide();
      expect(service.isLoading()).toBeTrue();
      
      service.hide();
      expect(service.isLoading()).toBeFalse();
    });

    it('should handle hide calls without corresponding show', () => {
      expect(service.isLoading()).toBeFalse();
      
      service.hide();
      expect(service.isLoading()).toBeFalse();
      
      service.show();
      expect(service.isLoading()).toBeTrue();
    });
  });

  describe('reset', () => {
    it('should reset loading state to false', () => {
      service.show();
      service.show();
      service.show();
      
      expect(service.isLoading()).toBeTrue();
      
      service.reset();
      
      expect(service.isLoading()).toBeFalse();
    });

    it('should reset loading count to zero', () => {
      service.show();
      service.show();
      service.show();
      
      service.reset();
      
      // After reset, one show should make it true
      service.show();
      expect(service.isLoading()).toBeTrue();
      
      // And one hide should make it false
      service.hide();
      expect(service.isLoading()).toBeFalse();
    });

    it('should work when already in initial state', () => {
      expect(service.isLoading()).toBeFalse();
      
      service.reset();
      
      expect(service.isLoading()).toBeFalse();
    });
  });

  describe('Complex Scenarios', () => {
    it('should handle nested loading operations', () => {
      // Outer operation starts
      service.show();
      expect(service.isLoading()).toBeTrue();
      
      // Inner operation 1 starts
      service.show();
      expect(service.isLoading()).toBeTrue();
      
      // Inner operation 2 starts
      service.show();
      expect(service.isLoading()).toBeTrue();
      
      // Inner operation 2 completes
      service.hide();
      expect(service.isLoading()).toBeTrue();
      
      // Inner operation 1 completes
      service.hide();
      expect(service.isLoading()).toBeTrue();
      
      // Outer operation completes
      service.hide();
      expect(service.isLoading()).toBeFalse();
    });

    it('should handle show-hide-show pattern', () => {
      service.show();
      expect(service.isLoading()).toBeTrue();
      
      service.hide();
      expect(service.isLoading()).toBeFalse();
      
      service.show();
      expect(service.isLoading()).toBeTrue();
      
      service.hide();
      expect(service.isLoading()).toBeFalse();
    });

    it('should handle reset in the middle of operations', () => {
      service.show();
      service.show();
      service.show();
      
      expect(service.isLoading()).toBeTrue();
      
      service.reset();
      expect(service.isLoading()).toBeFalse();
      
      // New operations after reset
      service.show();
      expect(service.isLoading()).toBeTrue();
      
      service.hide();
      expect(service.isLoading()).toBeFalse();
    });

    it('should use signals correctly', () => {
      // Test that isLoading is a signal (function)
      expect(typeof service.isLoading).toBe('function');
      
      // Test signal reactivity
      const initialValue = service.isLoading();
      service.show();
      const afterShow = service.isLoading();
      
      expect(initialValue).toBeFalse();
      expect(afterShow).toBeTrue();
    });
  });

  describe('Edge Cases', () => {
    it('should handle excessive hide calls', () => {
      service.show();
      
      for (let i = 0; i < 100; i++) {
        service.hide();
      }
      
      expect(service.isLoading()).toBeFalse();
      
      // Should still work normally after
      service.show();
      expect(service.isLoading()).toBeTrue();
    });

    it('should handle many concurrent operations', () => {
      const operationCount = 1000;
      
      for (let i = 0; i < operationCount; i++) {
        service.show();
      }
      
      expect(service.isLoading()).toBeTrue();
      
      for (let i = 0; i < operationCount - 1; i++) {
        service.hide();
      }
      
      expect(service.isLoading()).toBeTrue();
      
      service.hide();
      expect(service.isLoading()).toBeFalse();
    });
  });
});
