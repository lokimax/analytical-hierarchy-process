import { TestBed } from '@angular/core/testing';
import { ToastService, Toast } from './toast.service';

describe('ToastService', () => {
  let service: ToastService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(ToastService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  describe('show', () => {
    it('should emit toast with default type and duration', (done) => {
      const message = 'Test message';

      service.toasts$.subscribe((toast: Toast) => {
        expect(toast.message).toBe(message);
        expect(toast.type).toBe('info');
        expect(toast.duration).toBe(5000);
        expect(toast.id).toBeTruthy();
        expect(toast.id).toMatch(/^toast-\d+-[a-z0-9]+$/);
        done();
      });

      service.show(message);
    });

    it('should emit toast with custom type and duration', (done) => {
      const message = 'Custom toast';
      const type = 'warning';
      const duration = 3000;

      service.toasts$.subscribe((toast: Toast) => {
        expect(toast.message).toBe(message);
        expect(toast.type).toBe(type);
        expect(toast.duration).toBe(duration);
        done();
      });

      service.show(message, type, duration);
    });

    it('should generate unique IDs for each toast', () => {
      const ids = new Set<string>();
      let count = 0;

      service.toasts$.subscribe((toast: Toast) => {
        ids.add(toast.id);
        count++;
      });

      service.show('Message 1');
      service.show('Message 2');
      service.show('Message 3');

      expect(count).toBe(3);
      expect(ids.size).toBe(3);
    });
  });

  describe('success', () => {
    it('should emit success toast with default duration', (done) => {
      const message = 'Operation successful';

      service.toasts$.subscribe((toast: Toast) => {
        expect(toast.message).toBe(message);
        expect(toast.type).toBe('success');
        expect(toast.duration).toBe(5000);
        done();
      });

      service.success(message);
    });

    it('should emit success toast with custom duration', (done) => {
      const message = 'Success';
      const duration = 2000;

      service.toasts$.subscribe((toast: Toast) => {
        expect(toast.message).toBe(message);
        expect(toast.type).toBe('success');
        expect(toast.duration).toBe(duration);
        done();
      });

      service.success(message, duration);
    });
  });

  describe('error', () => {
    it('should emit error toast with default duration', (done) => {
      const message = 'An error occurred';

      service.toasts$.subscribe((toast: Toast) => {
        expect(toast.message).toBe(message);
        expect(toast.type).toBe('error');
        expect(toast.duration).toBe(5000);
        done();
      });

      service.error(message);
    });

    it('should emit error toast with custom duration', (done) => {
      const message = 'Error';
      const duration = 10000;

      service.toasts$.subscribe((toast: Toast) => {
        expect(toast.message).toBe(message);
        expect(toast.type).toBe('error');
        expect(toast.duration).toBe(duration);
        done();
      });

      service.error(message, duration);
    });
  });

  describe('warning', () => {
    it('should emit warning toast with default duration', (done) => {
      const message = 'Warning message';

      service.toasts$.subscribe((toast: Toast) => {
        expect(toast.message).toBe(message);
        expect(toast.type).toBe('warning');
        expect(toast.duration).toBe(5000);
        done();
      });

      service.warning(message);
    });

    it('should emit warning toast with custom duration', (done) => {
      const message = 'Warning';
      const duration = 7000;

      service.toasts$.subscribe((toast: Toast) => {
        expect(toast.message).toBe(message);
        expect(toast.type).toBe('warning');
        expect(toast.duration).toBe(duration);
        done();
      });

      service.warning(message, duration);
    });
  });

  describe('info', () => {
    it('should emit info toast with default duration', (done) => {
      const message = 'Information';

      service.toasts$.subscribe((toast: Toast) => {
        expect(toast.message).toBe(message);
        expect(toast.type).toBe('info');
        expect(toast.duration).toBe(5000);
        done();
      });

      service.info(message);
    });

    it('should emit info toast with custom duration', (done) => {
      const message = 'Info';
      const duration = 4000;

      service.toasts$.subscribe((toast: Toast) => {
        expect(toast.message).toBe(message);
        expect(toast.type).toBe('info');
        expect(toast.duration).toBe(duration);
        done();
      });

      service.info(message, duration);
    });
  });

  describe('Multiple toasts', () => {
    it('should emit multiple toasts in sequence', () => {
      const messages: string[] = [];

      service.toasts$.subscribe((toast: Toast) => {
        messages.push(toast.message);
      });

      service.success('First');
      service.error('Second');
      service.warning('Third');
      service.info('Fourth');

      expect(messages).toEqual(['First', 'Second', 'Third', 'Fourth']);
    });

    it('should handle rapid toast emissions', () => {
      let count = 0;

      service.toasts$.subscribe(() => {
        count++;
      });

      for (let i = 0; i < 10; i++) {
        service.show(`Message ${i}`);
      }

      expect(count).toBe(10);
    });
  });
});
