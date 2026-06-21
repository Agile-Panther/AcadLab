// @vitest-environment jsdom

import { render } from "@testing-library/react";
import { afterEach, beforeEach, describe, expect, it, vi } from "vitest";
import { CountUp } from "./CountUp";

class IntersectionObserverImediato {
  constructor(private readonly callback: IntersectionObserverCallback) {}

  observe(elemento: Element) {
    this.callback(
      [{ isIntersecting: true, target: elemento } as IntersectionObserverEntry],
      this as unknown as IntersectionObserver,
    );
  }

  disconnect() {}
  unobserve() {}
  takeRecords() {
    return [];
  }
  readonly root = null;
  readonly rootMargin = "0px";
  readonly thresholds = [0];
}

describe("CountUp", () => {
  beforeEach(() => {
    vi.stubGlobal("IntersectionObserver", IntersectionObserverImediato);
    vi.spyOn(performance, "now").mockReturnValue(0);
    vi.stubGlobal("requestAnimationFrame", (callback: FrameRequestCallback) => {
      callback(1200);
      return 1;
    });
  });

  afterEach(() => vi.restoreAllMocks());

  it("atualiza quando o valor chega depois da primeira renderização", () => {
    const tela = render(<CountUp end={0} />);

    tela.rerender(<CountUp end={120} />);

    expect(tela.container.textContent).toBe("120");
  });
});
